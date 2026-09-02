# S.T.E.M. Training 1.5 Beta

Read-only Health Connect integration:
- Settings: connect, granular system permissions, refresh, disconnect, and open Health Connect.
- Latest weight/body-fat observation within 29 days, source package and timestamp.
- Aggregated sleep duration for the last 24 hours with contributing sources.
- Latest nutrition record within 7 days with available calories and macros; explicitly not a daily total.
- Current health context displayed beside the local workout summary, not interpreted as historical workout-day data.
- Partial grants, missing values, revocation, unavailable providers and update/install flow handled.
- Health records remain in memory only while the screen is active. No export, backup, external AI transmission or write permissions.
- Age and muscle percentage are not imported; no automatic program changes.

Install over the current app, then Settings > Health Connect > Connect. Grant only the data you want to share. Your watch/scale/nutrition app must first write data to Health Connect.

Verification: release build and 20 JVM unit tests passed. Live Health Connect permission and data flow still requires device testing.
