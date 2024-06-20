## What is relation between resource,scope, permission and policy?

In Keycloak's authorization system, resources, scopes, permissions, and policies work together to control access to protected resources. Here's how they relate to each other:

### Resources
- **Definition**: Resources are entities or objects that you want to protect. They can represent API endpoints, files, database entries, or UI components.
- **Example**: A calendar application might have resources like `res::calendar` or `res::event`.

### Scopes
- **Definition**: Scopes define specific actions or levels of access that can be performed on a resource. They represent the permissions clients can request.
- **Example**: Common scopes for a calendar resource might include `view`, `create`, `edit`, and `delete`.

### Policies
- **Definition**: Policies define the rules and conditions under which access to resources and scopes is granted. They evaluate to either grant or deny access based on certain criteria.
- **Types**: Role-based, user-based, group-based, client-based, time-based, and script-based policies.
- **Example**: A role-based policy might grant access to users with the `admin` role, while a time-based policy might restrict access to business hours.

### Permissions
- **Definition**: Permissions combine resources, scopes, and policies to define comprehensive access control rules. They determine what resources can be accessed, what actions can be performed, and under what conditions.
- **Example**: A permission might specify that users can `edit` the `calendar` resource if they meet the criteria defined in the associated policies.

### Relationships

1. **Resource and Scope**:
    - Scopes are tied to resources to specify what actions can be performed on them.
    - Example: The `calendar` resource can have scopes like `view`, `create`, `edit`, `delete`.

2. **Policy**:
    - Policies determine the conditions under which access is granted. They do not directly grant access but define the logic for access decisions.
    - Example: A policy might state that only users with the `manager` role can `edit` the `calendar` resource.

3. **Permission**:
    - Permissions link resources, scopes, and policies together. They specify which resources and scopes are governed by which policies.
    - Example: A permission might grant `edit` access to the `calendar` resource if the `manager` role policy evaluates to true.

### Example Scenario

1. **Resource**: `res::calendar`
2. **Scopes**: `view`, `edit`
3. **Policies**:
    - `manager-role-policy`: Grants access if the user has the `manager` role.
    - `business-hours-policy`: Grants access during business hours.
4. **Permission**:
    - Name: `per::calendar::edit`
    - Resource: `res::calendar`
    - Scopes: `edit`
    - Policies: `manager-role-policy`, `business-hours-policy`
    - Decision Strategy: `Unanimous` (Both policies must grant access)

### Workflow

1. A user requests to `edit` the `calendar` resource.
2. The system checks the `per::calendar::edit` permission.
3. The permission evaluates the `edit` scope for the `res::calendar` resource.
4. The associated policies (`manager-role-policy`, `business-hours-policy`) are evaluated.
5. If both policies grant access (based on the `Unanimous` decision strategy), the user is allowed to `edit` the `calendar` resource.

This structured approach allows for flexible and fine-grained access control in applications using Keycloak.


