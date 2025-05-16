This style guide outlines the coding conventions for Java code developed at Liferay.

* **Readability:** Code should be easy to understand for all team members.
* **Maintainability:** Code should be easy to modify and extend.
* **Consistency:** Adhering to a consistent style across all projects improves
  collaboration and reduces errors.
* **Performance:** While readability is paramount, code should be efficient.

---

When possible, when variables are collected as values of a map or JSON object, each variable's name should match the key.

```java
boolean active = false;

JSONObject jsonObject = JSONUtil.put(
        "active", active
);
```

---

Variables of the generic type `Object` should be named `object` or should have `Object` as a suffix to the variable name.

```java
Object waitingObject;
```

---

Variable names for collections should have the collection type as a suffix to the variable name.

```java
JSONArray jsonArray;
```

---

Reduce indentation. Avoid loops with large method bodies by delegating to a function instead.

```java
private JSONArray _getJSONArray() {
  JSONArray jsonArray = _jsonFactory.createJSONArray();

  for (int i = 0; i < 100; i++) {
    jsonArray.put(_getJSONObject());
  }

  return jsonArray;
}

private JSONObject _getJSONObject() {
  JSONObject jsonObject = _jsonObject.createJSONObject();
  // complicated logic line 1
  // complicated logic line 2
  // complicated logic line 3
  // ...
  // complicated logic line 50
  return jsonObject;
}
```

---

Aside from `_log`, avoid declaring static final variables if they are only referenced once, and instead instantiate them where they are used.

---

Log messages at the start of a method or in the middle of the method body should start with verbs in present progressive.

```java
private void _doWork() {
  _log.debug("Adding data");
  _log.debug("Modifying data");
  _log.debug("Deleting data");

  // complicated logic line 1
  // complicated logic line 2
  // complicated logic line 3
}
```

---

Log messages near the end of a method should start with verbs in the past tense.

```java
private void _doWork() {
  // complicated logic line 1
  // complicated logic line 2
  // complicated logic line 3

  _log.debug("Added data");
  _log.debug("Modified data");
  _log.debug("Deleted data");
}
```

---

Log messages should avoid using `the`

```java
_log.debug("Unable to generate date");
```

---

If a method `getName` returns a hard-coded string value, this return value should match the class name with spaces.

```java
public class MySampleTestingServiceClass {
  public String getName() {
    return "My Sample Testing Service Class";
  }
}
```