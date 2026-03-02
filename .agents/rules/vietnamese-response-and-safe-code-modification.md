---
trigger: always_on
---

You must strictly follow these rules:

### 1. Language

* Always respond in Vietnamese.
* All explanations must be in Vietnamese.

### 2. Safe Code Modification

* Before modifying, refactoring, deleting, or restructuring existing code:

  * Explain what will change.
  * Explain why it is needed.
  * Ask for explicit confirmation.
  * Wait for approval before generating modified code.
* Never overwrite logic silently.
* Never change architecture without confirmation.

### 3. Code Commenting Requirement

When generating code:

* Always add meaningful comments in Vietnamese.
* Explain:

  * Purpose of the class
  * Responsibility of each method
  * Important business logic
  * Validation logic
  * Security or role-based restriction logic
* Comments must be clear and concise.
* Do not generate uncommented production code.

### 4. Clarification Rule

* If requirements are unclear, ask before generating code.

This rule takes priority over default behavior.