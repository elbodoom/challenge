package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void createAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    Account account = accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }

  @Test
  public void createDuplicateAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNegativeBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountEmptyAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void getAccount() throws Exception {
    String uniqueAccountId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
    this.accountsService.createAccount(account);
    this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId))
      .andExpect(status().isOk())
      .andExpect(
        content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
  }

  @Test
  public void transfer() throws Exception {
    this.accountsService.createAccount(new Account("1", BigDecimal.ONE));
    this.accountsService.createAccount(new Account("2", BigDecimal.ONE));
    this.mockMvc.perform(
            post("/v1/accounts/1/transfer?to=2&ammount=1"))
            .andExpect(
                    status().isCreated())
            .andExpect(
                    header().string("location", "/receipt/generated-id"));
  }

  @Test
  public void transfer_accountNotFound() throws Exception {
    this.mockMvc.perform(
            post("/v1/accounts/1/transfer?to=2&ammount=1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"accountId\":\"Id-123\",\"balance\":1000}"))
            .andExpect(
                    status().isNotFound());
  }

  @Test
  public void transfer_insufficientFunds() throws Exception {
    this.accountsService.createAccount(new Account("1", BigDecimal.ONE));
    this.accountsService.createAccount(new Account("2", BigDecimal.ONE));
    this.mockMvc.perform(
            post("/v1/accounts/1/transfer?to=2&ammount=100"))
            .andExpect(
                    status().isBadRequest())
            .andExpect(
                    status().reason("Insufficient funds"));
  }

  @Test(expected = Exception.class)
  public void transfer_invalidAmmount() throws Exception {
    this.accountsService.createAccount(new Account("1", BigDecimal.ONE));
    this.accountsService.createAccount(new Account("2", BigDecimal.ONE));
    this.mockMvc.perform(
            post("/v1/accounts/1/transfer?to=2&ammount=0"));

    //I would like to verify such test using the expect inspection as above, but I don't know why SpringMockMvc is not handling
    // the IllegalArgumentException, raised in the Account#withdraw
    //
    //  .andExpect(status().isInternalServerError());
  }
}
