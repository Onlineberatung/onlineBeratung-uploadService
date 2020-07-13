package de.caritas.cob.uploadservice.api.model.rocket.chat.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** PreferencesDTO for LoginResponseDTO */
@Getter
@Setter
@NoArgsConstructor
public class PreferencesDto {
  private String sidebarViewMode;
  private String sidebarGroupByType;
  private String sidebarHideAvatar;
  private String sidebarShowFavorites;
  private String convertAsciiEmoji;
  private String useEmojis;
  private String sendOnEnter;
  private String enableAutoAway;
  private String emailNotificationMode;
  private String muteFocusedConversations;
  private String saveMobileBandwidth;
  private String newMessageNotification;
  private String hideAvatars;
  private String desktopNotifications;
  private String idleTimeLimit;
  private String audioNotifications;
  private String desktopNotificationDuration;
  private String autoImageLoad;
  private String unreadAlert;
  private String hideFlexTab;
  private String newRoomNotification;
  private String roomCounterSidebar;
  private String collapseMediaByDefault;
  private String notificationsSoundVolume;
  private String messageViewMode;
  private String mobileNotifications;
  private String hideRoles;
  private String hideUsernames;
  private String sidebarShowUnread;
}
