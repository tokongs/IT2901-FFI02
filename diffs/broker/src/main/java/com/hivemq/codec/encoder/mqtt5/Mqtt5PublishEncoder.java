index 6fb7fc7..b080ae7 100644
--- a/broker/src/main/java/com/hivemq/codec/encoder/mqtt5/Mqtt5PublishEncoder.java
+++ b/broker/src/main/java/com/hivemq/codec/encoder/mqtt5/Mqtt5PublishEncoder.java
@@ -43,8 +43,7 @@ public class Mqtt5PublishEncoder extends Mqtt5MessageWithUserPropertiesEncoder<P
     private static final int FIXED_HEADER = MessageType.PUBLISH.ordinal() << 4;
 
     public Mqtt5PublishEncoder(final @NotNull MessageDroppedService messageDroppedService,
-                               final @NotNull SecurityConfigurationService securityConfigurationService){
-
+                               final @NotNull SecurityConfigurationService securityConfigurationService) {
         super(messageDroppedService, securityConfigurationService);
     }
 
