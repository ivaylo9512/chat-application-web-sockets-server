package unit.config;

import com.chat.app.ChatApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class ChatApplicationTest {
    @Test
    public void start(){
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {

            mocked.when(() -> SpringApplication.run(ChatApplication.class,
                            "foo", "bar"))
                    .thenReturn(Mockito.mock(ConfigurableApplicationContext.class));

            ChatApplication.main(new String[] { "arg1", "arg2" });

            mocked.verify(() -> { SpringApplication.run(ChatApplication.class,
                    "arg1", "arg2"); });

        }
    }
}
