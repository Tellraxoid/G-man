# Health Connect integration

Uses the stable androidx.health.connect:connect-client:1.1.0 SDK. Read-only permissions: weight, body fat, sleep and nutrition. No medical records, background/history extension, activity or write permission is requested.

Records are held in Compose state only, cleared on pause and reloaded after checking permission on resume. App settings persist only the opt-in flag, not health observations. Errors must never log health records. Disconnect stops reading and attempts to revoke all app Health Connect permissions without modifying source records.

Nutrition currently shows the latest single record, not a daily intake estimate. Age and muscle percentage are not available in this integration. No lean-mass-to-muscle conversion is performed. No health observations are sent to a model or used for automatic program rewriting.

## Device acceptance checklist

1. Install over previous app; existing workouts remain intact.
2. Open Settings > Health Connect, read the rationale, grant only weight.
3. Verify other metrics show access not granted, not zero.
4. With weight data present, verify timestamp, source package and value against Health Connect.
5. Grant sleep/nutrition/body-fat individually; verify empty records and partial nutrition macros.
6. Revoke weight in system settings; return and verify its value/source disappear.
7. Disconnect; confirm health UI stops loading and permissions are revoked.
8. Reopen app; confirm no stale health observations and no health values in JSON export.
9. Test unsupported Android/provider update paths separately.

No acceptance check should use fabricated data in the user's Health Connect store. Use a separate test device/profile for synthetic records.

References:
- https://developer.android.com/health-and-fitness/health-connect/get-started
- https://developer.android.com/health-and-fitness/health-connect/aggregate-data
- https://developer.android.com/jetpack/androidx/releases/health-connect
