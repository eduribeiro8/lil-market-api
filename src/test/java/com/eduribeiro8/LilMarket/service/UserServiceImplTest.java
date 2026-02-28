package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.UserRequestDTO;
import com.eduribeiro8.LilMarket.dto.UserResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.entity.UserRole;
import com.eduribeiro8.LilMarket.mapper.UserMapper;
import com.eduribeiro8.LilMarket.repository.UserRepository;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateUsernameException;
import com.eduribeiro8.LilMarket.rest.exception.UserNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO requestDTO;
    private User userFound;



    @Nested
    @DisplayName("Create User Tests")
    class createUser {

        @BeforeEach
        void setUp(){
            requestDTO = new UserRequestDTO(
                    "joaozinho123",
                    "teste123",
                    "João",
                    UserRole.ROLE_ADMIN,
                    true
            );
        }

        @Test
        @DisplayName("Deve salvar um novo usuário e retornar o DTO de resposta quando os dados forem válidos")
        void saveNewUser_Success() {
            //Arrange
            Long userId = 1L;
            String encryptPassword = "senha_secreta";

            User userToSave = User.builder()
                    .firstName("João")
                    .username("joaozinho123")
                    .password("teste123")
                    .role(UserRole.ROLE_ADMIN)
                    .active(true)
                    .build();

            User userPersisted = User.builder()
                    .id(userId)
                    .firstName("João")
                    .username("joaozinho123")
                    .password(encryptPassword)
                    .role(UserRole.ROLE_ADMIN)
                    .active(true)
                    .build();

            UserResponseDTO userResponseDTO = new UserResponseDTO(
                    1L,
                    "joaozinho123",
                    "João",
                    UserRole.ROLE_ADMIN,
                    true,
                    OffsetDateTime.now(),
                    null
            );

            Mockito.when(userMapper.toEntity(requestDTO)).thenReturn(userToSave);
            Mockito.when(userRepository.save(userToSave)).thenReturn(userPersisted);
            Mockito.when(passwordEncoder.encode("teste123")).thenReturn(encryptPassword);
            Mockito.when(userRepository.findByUsername("joaozinho123")).thenReturn(Optional.empty());
            Mockito.when(userMapper.toResponse(userPersisted)).thenReturn(userResponseDTO);

            //Act
            UserResponseDTO response = userService.save(requestDTO);

            //Assert
            assertNotNull(response);

            assertEquals(response.username(), userResponseDTO.username());
            assertEquals(userResponseDTO.id(), response.id());
            assertEquals(userResponseDTO.firstName(), response.firstName());
            assertEquals(userResponseDTO.userRole(), response.userRole());

            Mockito.verify(userRepository).findByUsername(requestDTO.username());
            Mockito.verify(passwordEncoder).encode(requestDTO.password());
            Mockito.verify(userRepository).save(any());
            Mockito.verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
        }

        @Test
        @DisplayName("Deve lançar DuplicateUsernameException quando o nome de usuário já existir")
        void saveNewUser_Fail_DuplicateUsername() {
            //Arrange

            Long userId = 1L;
            String encryptPassword = "senha_secreta";

            User userExisting = User.builder()
                    .id(userId)
                    .firstName("João")
                    .username("joaozinho123")
                    .password(encryptPassword)
                    .role(UserRole.ROLE_ADMIN)
                    .active(true)
                    .build();

            Mockito.when(userRepository.findByUsername("joaozinho123")).thenReturn(Optional.of(userExisting));

            //Act & Assert
            DuplicateUsernameException exception = assertThrows(DuplicateUsernameException.class, () ->
                userService.save(requestDTO)
            );
            assertTrue(exception.getMessage().contains("already registered"));

            Mockito.verify(userRepository, Mockito.never()).save(any());
            Mockito.verifyNoInteractions(passwordEncoder, userMapper);
        }
    }

    @Nested
    @DisplayName("Find User Tests")
    class findUser {

        @BeforeEach
        void setUp(){
            userFound = User.builder()
                    .id(1L)
                    .firstName("João")
                    .username("joaozinho123")
                    .role(UserRole.ROLE_ADMIN)
                    .active(true)
                    .build();
        }

        @Test
        @DisplayName("Deve retornar UserResponseDTO ao buscar por um username existente")
        void findByUsernameDTO_Success() {
            //Arrange
            String request = "joaozinho123";

            UserResponseDTO userResponseDTO = new UserResponseDTO(
                    1L,
                    "joaozinho123",
                    "João",
                    UserRole.ROLE_ADMIN,
                    true,
                    OffsetDateTime.now(),
                    null
            );

            Mockito.when(userRepository.findByUsername(request)).thenReturn(Optional.of(userFound));
            Mockito.when(userMapper.toResponse(userFound)).thenReturn(userResponseDTO);

            //Act
            UserResponseDTO responseDTO = userService.findUserByUsernameDTO(request);

            //Assert
            assertNotNull(responseDTO);
            assertEquals(responseDTO.id(), userResponseDTO.id());
            assertEquals(responseDTO.username(), userResponseDTO.username());
            assertEquals(responseDTO.userRole(), userResponseDTO.userRole());

            Mockito.verify(userRepository).findByUsername(any());
            Mockito.verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException ao buscar por um username que não existe")
        void findByUsernameDTO_Fail_UserNotFound() {
            //Arrange
            String request = "joaozinho123";
            Mockito.when(userRepository.findByUsername(request)).thenReturn(Optional.empty());

            //Act & Assert
            UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                    () ->
                        userService.findUserByUsernameDTO(request)
                    );
            assertTrue(exception.getMessage().contains("not found"));

            Mockito.verify(userRepository).findByUsername(any());
            Mockito.verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
        }

        @Test
        @DisplayName("Deve retornar User ao buscar por um username existente")
        void findByUsername_Success() {
            //Arrange
            String request = "joaozinho123";

            Mockito.when(userRepository.findByUsername(request)).thenReturn(Optional.of(userFound));

            //Act
            User response = userService.findUserByUsername(request);

            //Assert
            assertNotNull(response);
            assertEquals(response.getId(), userFound.getId());
            assertEquals(response.getUsername(), userFound.getUsername());
            assertEquals(response.getRole(), userFound.getRole());

            Mockito.verify(userRepository).findByUsername(any());
            Mockito.verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException ao buscar por um username que não existe")
        void findByUsername_Fail_UserNotFound() {
            //Arrange
            String request = "joaozinho123";
            Mockito.when(userRepository.findByUsername(request)).thenReturn(Optional.empty());

            //Act & Assert
            UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                    () ->
                        userService.findUserByUsername(request)
                    );
            assertTrue(exception.getMessage().contains("not found"));

            Mockito.verify(userRepository).findByUsername(any());
            Mockito.verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
        }

        @Test
        @DisplayName("Deve retornar a entidade User ao buscar por um ID existente")
        void findById_Success() {
            //Arrange
            Long request = 1L;

            Mockito.when(userRepository.findById(request)).thenReturn(Optional.of(userFound));

            //Act
            User userResponse = userService.findById(request);

            //Assert
            assertNotNull(userResponse);
            assertEquals(userResponse.getId(), userFound.getId());
            assertEquals(userResponse.getUsername(), userFound.getUsername());
            assertEquals(userResponse.getRole(), userFound.getRole());

            Mockito.verify(userRepository).findById(any());
            Mockito.verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException ao buscar por um ID que não existe")
        void findById_Fail_UserNotFound() {
            //Arrange
            Long request = 1L;
            Mockito.when(userRepository.findById(request)).thenReturn(Optional.empty());

            //Act

            //Assert
            UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                    () ->
                        userService.findById(request)
                    );
            assertTrue(exception.getMessage().contains("não encontrado"));

            Mockito.verify(userRepository, Mockito.times(1)).findById(any(Long.class));
            Mockito.verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
        }
    }

}