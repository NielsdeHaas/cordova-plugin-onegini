# Handle push message

<!-- toc -->

When your application receives a push message (which was sent by the Token Server) it can be handled by the Onegini Cordova Plugin.

## `onegini.mobileAuth.push.handlePushMessage`

This function handles received push message

- Requires a pushData in  form of a JSON object
- Push can be handled by the Onegini Cordova Plugin if the supplied JSON object contains the `og_transaction_id` and `og_profile_id` entry in the root or `content` key. Here are examples of accepted JSON object.

Example 1:
```JSON
{
  //...
  "og_transaction_id" : "223423",
  "og_profile_id" : "ZX123C"
  //...
}
```
Example 2:

```JSON
{
  // ...
  "content" : {
    // ...
    "og_transaction_id" : "223423",
    "og_profile_id" : "ZX123C"
    // ...  
  }
  // ...
}
```

**Example usage:**

```js
onegini.mobileAuth.push.handlePushMessage(data.additionalData)
	.catch((err) => navigator.notification.alert('Push message error: ' + err.description));

```

The error callback contains an object with the following properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8000 | The error code
| `description` | "Onegini: Internal plugin error" | Human readable error description