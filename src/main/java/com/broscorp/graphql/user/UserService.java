package com.broscorp.graphql.user;

import com.broscorp.graphql.security.annotation.Unsecured;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotation.GraphQLApi;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service
@GraphQLApi
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GraphQLQuery(name = "allUsers")
  @Unsecured
  public List<User> getAll() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

    return userRepository.findAll();
  }

  @GraphQLQuery(name = "userById")
  public User findById(@GraphQLNonNull @GraphQLArgument(name = "id") Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found by id = " + id));
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GraphQLMutation(name = "addUser")
  public User add(@GraphQLNonNull @GraphQLArgument(name = "username") String username) {
    User user = new User();
    user.setUsername(username);

    return userRepository.save(user);
  }
}
