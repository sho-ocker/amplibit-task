package org.example.inventory;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.ldap.LdapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(InventoryController.class)
@EnableMethodSecurity
public class InventoryControllerTest {

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private LdapService ldapService;

  @MockBean
  private InventoryService inventoryService;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(SecurityMockMvcConfigurers.springSecurity())
                             .build();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void testGetInventoryWithAdminAuthorizedRole() throws Exception {
    mockMvc.perform(get("/inventory"))
           .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  public void testGetInventoryWithUserAuthorizedRole() throws Exception {
    mockMvc.perform(get("/inventory"))
           .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "VIEWER")
  public void testGetInventoryWithViewerAuthorizedRole() throws Exception {
    mockMvc.perform(get("/inventory"))
           .andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  public void testGetInventoryUnauthorized() throws Exception {
    mockMvc.perform(get("/inventory"))
           .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void testAssignInventorySuccess() throws Exception {
    mockMvc.perform(post("/inventory/assign")
                        .param("inventoryId", "123e4567-e89b-12d3-a456-426614174000")
                        .param("userId", "123e4567-e89b-12d3-a456-426614174001")
                        .with(csrf()))
           .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  public void testAssignInventoryForbiddenForUser() throws Exception {
    mockMvc.perform(post("/inventory/assign")
                        .param("inventoryId", "123e4567-e89b-12d3-a456-426614174000")
                        .param("userId", "123e4567-e89b-12d3-a456-426614174001")
                        .with(csrf()))
           .andExpect(status().isForbidden());
  }

  @Test
  @WithAnonymousUser
  public void testAssignInventoryUnauthorized() throws Exception {
    mockMvc.perform(post("/inventory/assign")
                        .param("inventoryId", "123e4567-e89b-12d3-a456-426614174000")
                        .param("userId", "123e4567-e89b-12d3-a456-426614174001")
                        .with(csrf()))
           .andExpect(status().isUnauthorized());
  }

}
