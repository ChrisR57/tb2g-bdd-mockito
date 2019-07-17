package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

    @Mock
    OwnerService ownerService;

    @InjectMocks
    OwnerController ownerController;

    @Mock
    BindingResult bindingResult;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture()))
                .willAnswer(invocation -> {
                    List<Owner> ownerList = new ArrayList<>();
                    String name =invocation.getArgument(0);
                    if (name.equals("%Pearson%")) {
                        ownerList.add(new Owner(1L, "Joe", "Pearson"));
                        return ownerList;
                    }else if (name.equals("%NotFound%")){
                        return ownerList;
                    }else if (name.equals("%Found%")){
                        ownerList.add(new Owner(1L, "Joe", "Found"));
                        ownerList.add(new Owner(5L, "Fred", "Found"));
                        return ownerList;
                    }

                    throw new RuntimeException("Invalid Argument");
                });
    }

    @Test
    @DisplayName("Test Wildcard String 1 Entry Found")
    void processFindFormWildcardOneEntryFound() {
        //given
        Owner owner =  new Owner(1L, "Joe", "Pearson");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%Pearson%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("redirect:/owners/1").isEqualToIgnoringCase(viewName);
    }
    @Test
    @DisplayName("Test Wildcard String Not Found")
    void processFindFormWildcardNotFound() {
        //given
        Owner owner =  new Owner(1L, "Joe", "NotFound");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%NotFound%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/findOwners").isEqualToIgnoringCase(viewName);
    }

    @Test
    @DisplayName("Test Wildcard String Multiple Entries Found")
    void processFindFormWildcardFound() {
        //given
        Owner owner =  new Owner(1L, "Joe", "Found");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, Mockito.mock(Model.class));

        //then
        assertThat("%Found%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);
    }

    @Test
    @DisplayName("Test Process Creation with Errors")
    void processCreationFormHasErrors() {
        //given
        Owner owner =  new Owner(1L, "Fred", "Bloggs");
        given(bindingResult.hasErrors()).willReturn(true);

        //when
        String viewName = ownerController.processCreationForm(owner, bindingResult);

        //then
        assertThat(viewName).isEqualToIgnoringCase(OWNERS_CREATE_OR_UPDATE_OWNER_FORM);
    }

@Test
    @DisplayName("Test Process Creation without Errors")
    void processCreationFormNoErrors() {
    //given
    Owner owner =  new Owner(5L, "Fred", "Bloggs");
    given(bindingResult.hasErrors()).willReturn(false);
    given(ownerService.save(any())).willReturn(owner);

    //when
    String viewName = ownerController.processCreationForm(owner, bindingResult);

    //then
    assertThat(viewName).isEqualToIgnoringCase(REDIRECT_OWNERS_5);
    }
}