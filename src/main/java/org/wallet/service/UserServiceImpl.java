package org.wallet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.wallet.dto.ProfileType;
import org.wallet.dto.UserSave;
import org.wallet.dto.UserSaveDto;
import org.wallet.entity.Admin;
import org.wallet.entity.User;
import org.wallet.repository.AddressRepository;
import org.wallet.repository.CountryRepository;
import org.wallet.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final TransactionalOperator transactionalOperator;
  private final ProfileService profileService;
  private final AddressService addressService;
  private final CountryService countryService;
  private final AdminService adminService;
  private final IndividualService individualService;
  private final AuthorityService authorityService;
  private final MerchantService merchantService;

  @Override
  public Mono<User> save(UserSave userSave) {
    return transactionalOperator
      .transactional(
        Mono.defer(() ->
                     userRepository.save(userSave.getUser())
                       .flatMap(user -> {
                         userSave.getProfile().setUserUid(user.getUid());
                         return profileService.save(userSave.getProfile())
                           .flatMap(profile -> countryService.save(userSave.getCountry(), user.getUid())
                             .flatMap(country -> {
                                        userSave.getAddress().setCountryId(country.getId());
                                        return addressService.save(userSave.getAddress())
                                          .flatMap(address -> switch (user.getProfileType()) {
                                            case ADMIN -> {
                                              userSave.getAdmin().setProfileUid(profile.getUid());
                                              yield adminService.save(userSave.getAdmin());
                                            }
                                            case INDIVIDUAL -> {
                                              userSave.getIndividual().setProfileUid(profile.getUid());
                                              userSave.getIndividual().setAddressId(address.getId());
                                              yield individualService.save(userSave.getIndividual());
                                            }
                                            case MERCHANT -> {
                                              userSave.getMerchant().setProfileUid(profile.getUid());
                                              userSave.getMerchant().setAddressId(address.getId());
                                              yield merchantService.save(userSave.getMerchant());
                                            }
                                            case AUTHORITY -> authorityService.save(userSave.getAuthority());
                                          });
                                      }

                             )
                             .then(Mono.just(user)));
                       })
        ));
  }
}
