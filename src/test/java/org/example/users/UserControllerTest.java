package org.example.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(value = UserController.class)
@EnableMethodSecurity
public class UserControllerTest {

  private MockMvc mockMvc;

  @Autowired
  protected WebApplicationContext context;

  @MockBean
  private UserRepository userRepository;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(SecurityMockMvcConfigurers.springSecurity())
                             .build();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void testGetAllUsersWithAdminSuccess() throws Exception {
    mockMvc.perform(get("/users"))
           .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  public void testGetAllUsersWithUserForbidden() throws Exception {
    mockMvc.perform(get("/users"))
           .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "VIEWER")
  public void testGetAllUsersWithViewerForbidden() throws Exception {
    mockMvc.perform(get("/users"))
           .andExpect(status().isForbidden());
  }

  @Test
  @WithAnonymousUser
  public void testGetAllUsersUnauthorized() throws Exception {
    mockMvc.perform(get("/users"))
           .andExpect(status().isUnauthorized());
  }

}
