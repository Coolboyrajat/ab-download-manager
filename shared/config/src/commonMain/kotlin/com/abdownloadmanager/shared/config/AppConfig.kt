class AppConfig(
    val configRepository: ConfigRepository,
) {
    // Remote connection settings
    val remoteConnectionDeviceId = StringSetting("", "remote.deviceId", configRepository)
    val remoteConnectionEnabled = BooleanSetting(false, "remote.enabled", configRepository)
}