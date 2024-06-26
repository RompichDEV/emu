package com.eu.habbo.habbohotel.rooms;

public enum RoomChatMessageBubbles {
    NORMAL(0, "", true, true),
    ALERT(1, "", true, true),
    BOT(2, "", true, true),
    RED(3, "", true, true),
    BLUE(4, "", true, true),
    YELLOW(5, "", true, true),
    GREEN(6, "", true, true),
    BLACK(7, "", true, true),
    FORTUNE_TELLER(8, "", false, false),
    ZOMBIE_ARM(9, "", true, false),
    SKELETON(10, "", true, false),
    LIGHT_BLUE(11, "", true, true),
    PINK(12, "", true, true),
    PURPLE(13, "", true, true),
    DARK_YEWLLOW(14, "", true, true),
    DARK_BLUE(15, "", true, true),
    HEARTS(16, "", true, true),
    ROSES(17, "", true, true),
    UNUSED(18, "", true, true), //?
    PIG(19, "", true, true),
    DOG(20, "", true, true),
    BLAZE_IT(21, "", true, true),
    DRAGON(22, "", true, true),
    STAFF(23, "", false, true),
    BATS(24, "", true, false),
    MESSENGER(25, "", true, false),
    STEAMPUNK(26, "", true, false),
    THUNDER(27, "", true, true),
    PARROT(28, "", false, false),
    PIRATE(29, "", false, false),
    BOT_GUIDE(30, "", true, true),
    BOT_RENTABLE(31, "", true, true),
    SCARY_THING(32, "", true, false),
    FRANK(33, "", true, false),
    WIRED(34, "", false, true),
    GOAT(35, "", true, false),
    SANTA(36, "", true, false),
    AMBASSADOR(37, "acc_ambassador", false, true),
    RADIO(38, "", true, false),
    UNKNOWN_39(39, "", true, false),
    UNKNOWN_40(40, "", true, false),
    UNKNOWN_41(41, "", true, false),
    UNKNOWN_42(42, "", true, false),
    UNKNOWN_43(43, "", true, false),
    UNKNOWN_44(44, "", true, false),
    UNKNOWN_45(45, "", true, false),
    UNKNOWN_46(46, "", true, false),
    UNKNOWN_47(47, "", true, false),
    UNKNOWN_48(48, "", true, false),
    UNKNOWN_49(49, "", true, false),
    UNKNOWN_50(50, "", true, false),
    UNKNOWN_51(51, "", true, false),
    UNKNOWN_52(52, "", true, false),
    UNKNOWN_53(53, "", true, false),
    UNKNOWN_54(54, "", true, false),
    UNKNOWN_55(55, "", true, false),
    UNKNOWN_56(56, "", true, false),
    UNKNOWN_57(57, "", true, false),
    UNKNOWN_58(58, "", true, false),
    UNKNOWN_59(59, "", true, false),
    UNKNOWN_60(60, "", true, false),
    UNKNOWN_61(61, "", true, false),
    UNKNOWN_62(62, "", true, false),
    UNKNOWN_63(63, "", true, false),
    UNKNOWN_64(64, "", true, false),
    UNKNOWN_65(65, "", true, false),
    UNKNOWN_66(66, "", true, false),
    UNKNOWN_67(67, "", true, false),
    UNKNOWN_68(68, "", true, false),
    UNKNOWN_69(69, "", true, false),
    UNKNOWN_70(70, "", true, false),
    UNKNOWN_71(71, "", true, false),
    UNKNOWN_72(72, "", true, false),
    UNKNOWN_73(73, "", true, false),
    UNKNOWN_74(74, "", true, false),
    UNKNOWN_75(75, "", true, false),
    UNKNOWN_76(76, "", true, false),
    UNKNOWN_77(77, "", true, false),
    UNKNOWN_78(78, "", true, false),
    UNKNOWN_79(79, "", true, false),
    UNKNOWN_80(80, "", true, false),
    UNKNOWN_81(81, "", true, false),
    UNKNOWN_82(82, "", true, false),
    UNKNOWN_83(83, "", true, false),
    UNKNOWN_84(84, "", true, false),
    UNKNOWN_85(85, "", true, false),
    UNKNOWN_86(86, "", true, false),
    UNKNOWN_87(87, "", true, false),
    UNKNOWN_88(88, "", true, false),
    UNKNOWN_89(89, "", true, false),
    UNKNOWN_90(90, "", true, false),
    UNKNOWN_91(91, "", true, false),
    UNKNOWN_92(92, "", true, false),
    UNKNOWN_93(93, "", true, false),
    UNKNOWN_94(94, "", true, false),
    UNKNOWN_95(95, "", true, false);

    private final int type;
    private final String permission;
    private final boolean overridable;
    private final boolean triggersTalkingFurniture;

    RoomChatMessageBubbles(int type, String permission, boolean overridable, boolean triggersTalkingFurniture) {
        this.type = type;
        this.permission = permission;
        this.overridable = overridable;
        this.triggersTalkingFurniture = triggersTalkingFurniture;
    }

    public static RoomChatMessageBubbles getBubble(int bubbleId) {
        try {
            return values()[bubbleId];
        } catch (Exception e) {
            return NORMAL;
        }
    }

    public int getType() {
        return this.type;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean isOverridable() {
        return this.overridable;
    }

    public boolean triggersTalkingFurniture() {
        return this.triggersTalkingFurniture;
    }
}
