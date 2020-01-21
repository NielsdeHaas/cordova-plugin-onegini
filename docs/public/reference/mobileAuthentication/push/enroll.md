# Enroll for mobile authentication with push

<!-- toc -->

Authenticated users can enroll for mobile authentication with push, allowing them to receive and respond to push notifications.

A prerequisite for this method is that the user is already enrolled for [regular mobile authentication (without push)](../enroll.md).

## `onegini.mobileAuth.push.enroll`

This function enrolls the currently logged in user for mobile authentication.
- Requires a push token in form of a string

**Example enrollment for a logged in user:**

```js
onegini.mobileAuth.push.enroll(pushToken)
    .then(() => {
      alert("Enrollment success!");
    })
    .catch((err) => {
      alert("Enrollment error!\n\n" + err.description);
    });
```

The error callback contains an object with the following properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8000 | The error code
| `description` | "Onegini: Internal plugin error" | Human readable error description
