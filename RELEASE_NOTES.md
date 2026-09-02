# S.T.E.M. Training 1.5.2 Beta

- Rest timer stays visible above navigation during an active workout, even before starting or after skipping rest.
- Manual start, large countdown, +30 seconds, skip and persistent completion state.
- Working-set auto-start and superset behavior preserved.
- Rest completion notification uses the existing high-importance channel, alarm category and explicit sound configuration for newly created channels. Existing user notification preferences remain respected.
- Direct shortcut to the timer channel settings and a 5-second signal check (does not replace a running rest timer).
- Restarting/skipping clears stale notifications; permission revocation does not crash completion.

Heads-up banners depend on Android/device notification settings and Do Not Disturb. Enable floating notifications in the rest completion channel. Background alarms remain inexact and can be delayed by Android battery management. No full-screen overlays or new permissions.

Install over the existing app without uninstalling. No workout database changes.

Verification: release build and JVM tests; physical-device banner and timer visibility require checking on your phone.
