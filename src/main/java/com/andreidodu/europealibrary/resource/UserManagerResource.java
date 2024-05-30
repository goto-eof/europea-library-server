package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CommonGenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.UserEnableDTO;
import com.andreidodu.europealibrary.dto.security.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;

@RequestMapping("/api/v1/user")
@Tag(name = "User Manager", description = "Allows to manage Europea Library users")
public interface UserManagerResource {

    @AllowOnlyAdministrator
    @GetMapping(path = "/id/{id}")
    @Operation(summary = "Provides user information by user id",
            description = "Returns user information by id. Only the administrator is enabled to use this feature.",
            security = @SecurityRequirement(
                    name = "administration-auth",
                    scopes = "read:users"),
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "Authorization",
                            required = true, description = "JWT token that identifies user session",
                            allowEmptyValue = false, allowReserved = true,
                            schema = @Schema(
                                    type = "string",
                                    format = "string",
                                    description = "the generate JWT token",
                                    accessMode = Schema.AccessMode.READ_ONLY)
                    ),
                    @Parameter(in = ParameterIn.COOKIE, name = "device-id",
                            required = true, description = "Device ID that identifies the users device and protects system by XSS attack",
                            allowEmptyValue = false, allowReserved = true,
                            schema = @Schema(
                                    type = "string",
                                    format = "string",
                                    description = "Unique string",
                                    accessMode = Schema.AccessMode.READ_ONLY)
                    ),
                    @Parameter(in = ParameterIn.PATH, name = "id",
                            required = true, description = "user identifier",
                            allowEmptyValue = false, allowReserved = true,
                            schema = @Schema(
                                    type = "long",
                                    format = "number",
                                    description = "the row identifier"))
            },
            responses = {
                    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Item retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_UNAUTHORIZED, description = "Not authorized because of wrong JWT"),
                    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_BAD_REQUEST, description = "Bad request. Invalid input JSON.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    ResponseEntity<UserDTO> getById(@NotNull @PathVariable Long id);


    @AllowOnlyAdministrator
    @PostMapping
    @Operation(summary = "Provides a list of users by cursorId",
            description = "Returns a limited number of users that are registered to the platform. Only the administrator is enabled to use this feature.",
            security = @SecurityRequirement(
                    name = "administration-auth",
                    scopes = "read:users"),
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "Authorization",
                            required = true, description = "JWT token that identifies user session",
                            allowEmptyValue = false, allowReserved = true,
                            schema = @Schema(
                                    type = "string",
                                    format = "string",
                                    description = "the generate JWT token",
                                    accessMode = Schema.AccessMode.READ_ONLY)
                    ),
                    @Parameter(in = ParameterIn.COOKIE, name = "device-id",
                            required = true, description = "Device ID that identifies the users device and protects system by XSS attack",
                            allowEmptyValue = false, allowReserved = true,
                            schema = @Schema(
                                    type = "string",
                                    format = "string",
                                    description = "Unique string",
                                    accessMode = Schema.AccessMode.READ_ONLY)
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Item retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_UNAUTHORIZED, description = "Not authorized because of wrong JWT"),
                    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_BAD_REQUEST, description = "Bad request. Invalid input JSON.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    ResponseEntity<CommonGenericCursoredResponseDTO<UserDTO>> getAllPaginated(
            @RequestBody(required = true,
                    description = "Payload that provides information about filtration criteria",
                    content = @Content(
                            schema = @Schema(implementation = CommonCursoredRequestDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody @Valid CommonCursoredRequestDTO commonCursoredRequestDTO);


    @AllowOnlyAdministrator
    @PatchMapping(path = "/enable/id/{id}")
    @Operation(summary = "Enables/Disables a user",
            description = "Allows to enable/disable a user to user the platform. Only the administrator is enabled to use this feature.",
            security = @SecurityRequirement(
                    name = "administration-auth",
                    scopes = "read:users"),
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "Authorization",
                            required = true, description = "JWT token that identifies user session",
                            allowEmptyValue = false, allowReserved = true,
                            schema = @Schema(
                                    type = "string",
                                    format = "string",
                                    description = "the generate JWT token",
                                    accessMode = Schema.AccessMode.READ_ONLY)
                    ),
                    @Parameter(in = ParameterIn.COOKIE, name = "device-id",
                            required = true, description = "Device ID that identifies the users device and protects system by XSS attack",
                            allowEmptyValue = false, allowReserved = true,
                            schema = @Schema(
                                    type = "string",
                                    format = "string",
                                    description = "Unique string",
                                    accessMode = Schema.AccessMode.READ_ONLY)
                    ),
                    @Parameter(in = ParameterIn.PATH, name = "id",
                            required = true, description = "user identifier",
                            allowEmptyValue = false, allowReserved = true,
                            schema = @Schema(
                                    type = "long",
                                    format = "number",
                                    description = "the row identifier"))
            },
            responses = {
                    @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_OK, description = "Item retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_UNAUTHORIZED, description = "Not authorized because of wrong JWT"),
                    @ApiResponse(responseCode = "" + +HttpURLConnection.HTTP_BAD_REQUEST, description = "Bad request. Invalid input JSON.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    ResponseEntity<UserDTO> enable(@PathVariable Long id,
                                   @RequestBody(required = true,
                                           description = "Payload with a single property that indicates if user should be enabled or not.",
                                           content = @Content(
                                                   schema = @Schema(implementation = UserEnableDTO.class)))
                                   @Valid @org.springframework.web.bind.annotation.RequestBody UserEnableDTO userEnableDTO);


}
