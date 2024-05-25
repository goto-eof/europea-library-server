package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.constants.CookieConst;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.security.AuthRequestDTO;
import com.andreidodu.europealibrary.dto.security.AuthResponseDTO;
import com.andreidodu.europealibrary.dto.security.ChangePasswordRequestDTO;
import com.andreidodu.europealibrary.dto.security.PasswordResetEmailRequestDTO;
import com.andreidodu.europealibrary.dto.security.PasswordResetRequestDTO;
import com.andreidodu.europealibrary.dto.security.RegistrationRequestDTO;
import com.andreidodu.europealibrary.dto.security.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;

@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication and Registration", description = "Allows to login/register/change password/reset password")
public interface AuthenticationAndRegistrationResource {
    @PostMapping("/login")
    @Operation(summary = "Login to the system", description = "Returns an access token if username and password match. Otherwise it returns HTTP error code 401 Unauthorized")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Logged in successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_UNAUTHORIZED, description = "Not authorized because of wrong username or password")
    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_BAD_REQUEST, description = "Bad request. Invalid JSON.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<AuthResponseDTO> login(HttpServletResponse response, @Valid @RequestBody AuthRequestDTO authRequestDTO);

    @GetMapping("/me")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "User retrieved with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<UserDTO> getUser(Authentication authentication);

    @PostMapping("/register")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Registered and logged in with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<AuthResponseDTO> register(HttpServletResponse response, @Valid @RequestBody RegistrationRequestDTO registrationRequestDTO);

    @PostMapping("/password/change")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Password changed with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<OperationStatusDTO> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, Authentication authentication);

    @PostMapping("/password/reset/sendEmail")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Email sent with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<OperationStatusDTO> sendPasswordRecoveryEmail(@Valid @RequestBody PasswordResetEmailRequestDTO passwordResetEmailRequestDTO);

    @PostMapping("/password/reset")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Password resetted with success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<OperationStatusDTO> passwordReset(@Valid @RequestBody PasswordResetRequestDTO passwordResetRequestDTO);

    @PostMapping("/logout")
    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "logout a specific agent from the system", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    ResponseEntity<OperationStatusDTO> logout(@CookieValue(CookieConst.COOKIE_NAME_DEVICE_ID) String deviceId, HttpServletRequest request);
}
