package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.auth.AuthRequestDTO;
import com.andreidodu.europealibrary.dto.auth.AuthResponseDTO;
import com.andreidodu.europealibrary.dto.auth.ChangePasswordRequestDTO;
import com.andreidodu.europealibrary.dto.auth.PasswordResetEmailRequestDTO;
import com.andreidodu.europealibrary.dto.auth.PasswordResetRequestDTO;
import com.andreidodu.europealibrary.dto.auth.RegistrationRequestDTO;
import com.andreidodu.europealibrary.dto.auth.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.HttpURLConnection;

@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication and Registration", description = "Allows to login/register/change password/reset password")
public interface AuthResource {
    @PostMapping("/login")
    @Operation(summary = "Login to the system", description = "Returns an access token if username and password match. Otherwise it returns HTTP error code 401 Unauthorized")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Logged in successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_UNAUTHORIZED, description = "Not authorized because of wrong username or password")
    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_BAD_REQUEST, description = "Bad request. Invalid JSON.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO);

    @GetMapping("/me")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "User retrieved with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<UserDTO> getUser(Authentication authentication);

    @PostMapping("/register")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Registered and logged in with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO registrationRequestDTO);

    @PostMapping("/password/change")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Password changed with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<OperationStatusDTO> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, Authentication authentication);

    @PostMapping("/password/reset/sendEmail")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Email sent with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<OperationStatusDTO> sendPasswordRecoveryEmail(@Valid @RequestBody PasswordResetEmailRequestDTO passwordResetEmailRequestDTO);

    @PostMapping("/password/reset")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Password resetted with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<OperationStatusDTO> passwordReset(@Valid @RequestBody PasswordResetRequestDTO passwordResetRequestDTO);
}
