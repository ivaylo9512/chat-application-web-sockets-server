package services;
import repositories.base.UserRepository;
import exceptions.UsernameExistsException;
import models.UserDetails;
import models.UserModel;
import models.specs.UserSpec;
import services.base.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService,UserDetailsService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserModel> findAll() { ;
        return userRepository.findAll();
    }

    @Override
    public UserModel findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist."));
    }

    @Override
    public UserModel register(UserSpec userSpec, String role) {
        UserModel userModel = userRepository.findByUsername(userSpec.getUsername());

        if (userModel != null) {
            throw new UsernameExistsException("Username is already taken.");
        }

        userModel = new UserModel(userSpec, role);
        userModel.setPassword(BCrypt.hashpw(userModel.getPassword(),BCrypt.gensalt(4)));
        return userRepository.save(userModel);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserModel userModel = userRepository.findByUsername(username);
        if(userModel == null){
            throw new BadCredentialsException("Bad credentials");
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userModel.getRole()));

        return new UserDetails(userModel,authorities);
    }

    @Override
    public UserModel changeUserInfo(int loggedUser, UserSpec userSpec){
        UserModel user = userRepository.findById(loggedUser)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));
        user.setFirstName(userSpec.getFirstName());
        user.setLastName(userSpec.getLastName());
        user.setAge(userSpec.getAge());
        user.setCountry(userSpec.getCountry());

        return userRepository.save(user);
    }
}
