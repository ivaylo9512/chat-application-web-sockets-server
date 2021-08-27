package unit;

import com.chat.app.exceptions.FileFormatException;
import com.chat.app.models.File;
import com.chat.app.repositories.base.FileRepository;
import com.chat.app.services.FileServiceImpl;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileService {
    @Mock
    private FileRepository fileRepository;

    @Spy
    @InjectMocks
    private FileServiceImpl fileService;

    @Test
    public void generate() {
        MockMultipartFile file = new MockMultipartFile(
                "image132",
                "image132.png",
                "image/png",
                "image132".getBytes());

        File savedFile = fileService.generate(file, "savedName", "image");

        assertEquals(savedFile.getName(), "savedName.png");
        assertEquals(savedFile.getType(), "image/png");
    }

    @Test
    public void create_WhenTypeDoesNotMatch_FileFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "text132",
                "text132.txt",
                "text/plain",
                "text132".getBytes());

        FileFormatException thrown = assertThrows(FileFormatException.class,
                () -> fileService.create(file, "savedName", "image"));

        assertEquals(thrown.getMessage(), "File should be of type image");
    }

    @Test
    public void create() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "image132",
                "image132.png",
                "image/png",
                "image132".getBytes());

        File generatedFile = new File("savedName.png", 22.0, "image/png");

        doNothing().when(fileService).save(generatedFile, file);
        when(fileService.generate(file, "savedName.png", "image")).thenReturn(generatedFile);
        when(fileRepository.save(generatedFile)).thenReturn(generatedFile);

        File savedFile = fileService.create(file, "savedName.png", "image");

        assertEquals(savedFile.getName(), "savedName.png");
        assertEquals(savedFile.getType(), "image/png");
    }

    @Test
    public void createAndSave() throws Exception{
        FileInputStream input = new FileInputStream("./uploads/test.txt");
        MultipartFile multipartFile = new MockMultipartFile("test", "test.txt", "text/plain",
                IOUtils.toByteArray(input));

        fileService.create(multipartFile, "test2", "text");

        assertTrue(new java.io.File("./uploads/test2.txt").exists());
    }
}
