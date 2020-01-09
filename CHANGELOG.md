# Changelog

## v0.5.0

- Changed API for startAdvertising to accept a name as an alternative to the random name assigned by the plugin for the device
- When setState is triggered more than once, it clears all endpoints and stops discovery, and restarts that discovery. Basically attempts to reset the plugin. 

