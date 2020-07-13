package de.caritas.cob.uploadservice.api.helper;

import static de.caritas.cob.uploadservice.helper.TestConstants.USERNAME_DECODED;
import static de.caritas.cob.uploadservice.helper.TestConstants.USERNAME_ENCODED;
import static de.caritas.cob.uploadservice.helper.TestConstants.USERNAME_INVALID_ENCODED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.caritas.cob.uploadservice.api.exception.HelperException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserHelperTest {

  private UserHelper userHelper;

  @Before
  public void setup() {
    this.userHelper = new UserHelper();
  }

  @Test
  public void
      encodeUsername_Should_ReturnEncodedUsername_WhenDecodedUsernameIsGiven() {
    assertEquals(USERNAME_ENCODED, userHelper.encodeUsername(USERNAME_DECODED));
  }

  @Test
  public void encodeUsername_Should_ReturnEncodedUsername_WhenEncodedUsernameIsGiven() {
    assertEquals(USERNAME_ENCODED, userHelper.encodeUsername(USERNAME_ENCODED));
  }

  @Test
  public void decodeUsername_Should_ReturnDecodedUsername_WhenEncodedUsernameIsGiven() {
    assertEquals(USERNAME_DECODED, userHelper.decodeUsername(USERNAME_ENCODED));
  }

  @Test
  public void decodeUsername_Should_ReturnDecodedUsername_WhenDecodedUsernameIsGiven() {
    assertEquals(USERNAME_DECODED, userHelper.decodeUsername(USERNAME_DECODED));
  }

  @Test
  public void decodeUsername_Should_ThrowHelperException_WhenDecodingFails() {
    try {
      userHelper.decodeUsername(USERNAME_INVALID_ENCODED);
      fail("Expected exception: HelperException");
    } catch (HelperException helperException) {
      assertTrue("Excepted HelperException thrown", true);
    }
  }
}
