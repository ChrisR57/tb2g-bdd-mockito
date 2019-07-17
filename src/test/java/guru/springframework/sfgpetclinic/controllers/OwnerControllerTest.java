package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Test
    @DisplayName("Test Wildcard String (ProcessFindForm)")
    void processFindFormWildcardString() {
        //given
        Owner owner =  new Owner(1L, "Joe", "Pearson");
        List<Owner> ownerList = new ArrayList<>();
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        given(ownerService.findAllByLastNameLike(captor.capture())).willReturn(ownerList);

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%Pearson%").isEqualToIgnoringCase(captor.getValue());
    }
    @Test
    @DisplayName("Test Wildcard String (ProcessFindForm) using Annotations")
    void processFindFormWildcardStringAnnotion() {
        //given
        Owner owner =  new Owner(1L, "Joe", "Pearson");
        List<Owner> ownerList = new ArrayList<>();
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture())).willReturn(ownerList);

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%Pearson%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
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