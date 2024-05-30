package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CommonGenericCursoredResponseDTO;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
    ResponseEntity<UserDTO> getById(@PathVariable Long id);


    @AllowOnlyAdministrator
    @GetMapping
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
                    description = "Payload tat provides information about filtration criteria",
                    content = @Content(
                            schema = @Schema(implementation = CommonCursoredRequestDTO.class)))
            CommonCursoredRequestDTO commonCursoredRequestDTO);


    @AllowOnlyAdministrator
    @PatchMapping(path = "/enable/id/{id}")
    @Operation(summary = "Enables a user",
            description = "Allows to enable a user to user the platform. Only the administrator is enabled to use this feature.",
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
    ResponseEntity<UserDTO> enable(@PathVariable Long id);

    @AllowOnlyAdministrator
    @PatchMapping(path = "/disable/id/{id}")
    @Operation(summary = "Disables a user",
            description = "Allows to disable a user to avoid the use of the platform. Only the administrator is enabled to use this feature.",
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
    ResponseEntity<UserDTO> disable(@PathVariable Long id);

}
