# Verify if push message can be handled

<!-- toc -->

When your application receives a push message, you can verify whether it handleable by the Onegini Cordova Plugin.

## `onegini.mobileAuth.push.canHandlePushMessage`

This function verifies if push message can be handled.

- Requires a pushData in form of a JSON object
- Supplied JSON object should contain the `og_transaction_id` and `og_profile_id` entry in the root or `content` key. Here are examples of accepted JSON object.

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
if (onegini.mobileAuth.push.canHandlePushMessage(data.additionalData)) { 
    onegini.mobileAuth.push.handlePushMessage(data.additionalData)
}
```