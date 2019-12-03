package by.danilov.cs.lab2.controller;

import by.danilov.cs.lab2.domain.FetchResponse;
import by.danilov.cs.lab2.domain.FileEntity;
import by.danilov.cs.lab2.exception.BadRequestException;
import by.danilov.cs.lab2.security.store.KeyStore;
import by.danilov.cs.lab2.service.AESService;
import by.danilov.cs.lab2.util.AttributesUtil;
import by.danilov.cs.lab2.util.Verifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

import static by.danilov.cs.lab2.util.ResponseBuilder.buildFetchResponse;

@RestController
@RequestMapping("/api")
public class FileController {

    private static final String FILE_PATH_PATTERN = "src\\main\\resources\\store\\files\\{0}.txt";
    private static final String FILE_PATH_PATTERN_USER_SPECIFIC = "src\\main\\resources\\store\\files\\{0}\\{1}.txt";
    private static final String FILE_FOLDER_USER_SPECIFIC = "src\\main\\resources\\store\\files\\{0}";
    private final AESService aesService;

    public FileController(AESService aesService, KeyStore keyStore) {
        this.aesService = aesService;
    }

    @PostMapping("/file/get")
    public ResponseEntity<FetchResponse> getText(@RequestBody FileEntity fileEntity,
                                                 HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (!Verifier.verify(request)) {
                return buildFetchResponse("400 Bad Request",
                        HttpStatus.BAD_REQUEST, null, false);
            }

            String text = readFile(decrypt(fileEntity.getFileName(), session));
            String encryptedText = encrypt(text, session);

            FetchResponse fetchResponse = new FetchResponse();
            fetchResponse.addAttribute("text", encryptedText);
            AttributesUtil.setRequestIdAttribute(session, fetchResponse);

            return ResponseEntity.ok(fetchResponse);

        } catch (IOException e) {
            return buildFetchResponse("File was not found",
                    HttpStatus.NOT_FOUND, null, false);

        } catch (BadRequestException e) {
            return buildFetchResponse(e.getMessage(),
                    HttpStatus.BAD_REQUEST, null, false);
        }
    }

    @PostMapping("/file/upload")
    public ResponseEntity<FetchResponse> loadFile(@RequestBody FileEntity fileEntity, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (!Verifier.verify(request)) {
            return buildFetchResponse("400 Bad Request",
                    HttpStatus.BAD_REQUEST, null, false);
        }

        try {
            String fileName = decrypt(fileEntity.getFileName(), session);
            String fileContent = decrypt(fileEntity.getContent(), session);
            String userLoginHash = (String) session.getAttribute("USER_ID");

            writeFile(fileName, fileContent, userLoginHash);

            return buildFetchResponse(
                    "File uploaded successfully",
                    HttpStatus.OK, session, true);

        } catch (BadRequestException e) {
            return buildFetchResponse("400 Bad Request",
                    HttpStatus.BAD_REQUEST, null, false);
        } catch (IOException e) {
            return buildFetchResponse("Cannot create file",
                    HttpStatus.NOT_ACCEPTABLE, null, false);
        }
    }

    private String readFile(String filename) throws IOException {
        String fullPath = MessageFormat.format(FILE_PATH_PATTERN, filename);
        return Files.readString(Paths.get(fullPath));
    }

    private void writeFile(String filename, String fileContent, String userLoginHash) throws IOException {
        String fullPath = MessageFormat.format(FILE_PATH_PATTERN_USER_SPECIFIC, userLoginHash, filename);
        String folderPath = MessageFormat.format(FILE_FOLDER_USER_SPECIFIC, userLoginHash);

        if (!Files.exists(Paths.get(folderPath))) {
            Files.createDirectory(Paths.get(folderPath));
        }

        Files.write(Paths.get(fullPath), fileContent.getBytes());
    }

    private String encrypt(String encryptedText, HttpSession session) throws BadRequestException {
        try {
            String key = (String) session.getAttribute("token");
            String iv = (String) session.getAttribute("iv");
            return aesService.encryptAES_CBC(encryptedText, key, iv);
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }

    private String decrypt(String encryptedText, HttpSession session) throws BadRequestException {
        try {
            String key = (String) session.getAttribute("token");
            String iv = (String) session.getAttribute("iv");
            return aesService.decryptAES_CBC(encryptedText, key, iv);
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }

}
