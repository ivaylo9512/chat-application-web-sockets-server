package integration;

import com.chat.app.config.AppConfig;
import com.chat.app.config.MethodSecurityConfigurer;
import com.chat.app.config.SecurityConfig;
import com.chat.app.config.TestWebConfig;
import com.chat.app.models.specs.RegisterSpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.ServletContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class, TestWebConfig.class, SecurityConfig.class, MethodSecurityConfigurer.class})
@WebAppConfiguration(value = "src/main/java/com/chat/app")
@WebMvcTest
public class Users {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void assertConfig_assertUserController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(webApplicationContext.getBean("userController"));
    }

    @WithMockUser(value = "spring")
    @Test
    public void register() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(new RegisterSpec("username", "password", "password"));
        mockMvc.perform(post("/api/users/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "spring")
    @Test
    public void login() throws Exception {
        mockMvc.perform(post("/api/users/login")
                .content("{username: 'username', password: 'password'}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
