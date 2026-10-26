# Security Specification — Aditya Online Center (`security_spec.md`)

## 1. Data Invariants
1. **Authentication Prerequisite**: Unauthenticated callers (`request.auth == null`) cannot read or write any document in `/services`, `/orders`, `/users`, or `/admins`.
2. **Service Catalogue Authority**: Only authorized administrators (`isAdmin()`) can `create`, `update`, or `delete` documents in `/services/{serviceId}`. Authenticated customers can `get` and `list` services scoped by `tenantId == 'aditya_online_center'` or `active == true`.
3. **Order Ownership & Trusted Pricing**:
   - A customer can only `create` an order where `userId == request.auth.uid` and `customerUid == request.auth.uid`.
   - Every newly created order MUST reference an existing, active service (`exists(/databases/$(database)/documents/services/$(incoming().serviceId))` and `get(...).data.active == true`) and its `priceSnapshot` MUST match the authoritative `get(...).data.price`.
   - Every newly created order by a customer MUST start with `status == 'pending'` and `paymentStatus in ['unpaid', 'pending_verification']` (never `'verified'`).
4. **Order Isolation & Terminal State Locking**:
   - Customers can only `get` or `list` orders where `resource.data.userId == request.auth.uid`.
   - Once an order reaches `'completed'` or `'cancelled'`, customers cannot update any field on that order.
   - Customers can only update `upiReference` + `paymentStatus` (to `'pending_verification'`) or cancel a `'pending'` order (`status` to `'cancelled'`). Only `isAdmin()` can transition `status` to `'processing'`, `'action_required'`, or `'completed'`, or set `paymentStatus` to `'verified'`.
5. **PII Isolation (`/users/{userId}`)**:
   - Only `request.auth.uid == userId` or `isAdmin()` can `get` a customer's profile. Non-owners are denied.

## 2. The "Dirty Dozen" Adversarial Payloads
1. **Unauthenticated Catalogue/Order Read**: Unauthenticated client queries `/services` or `/orders` -> `PERMISSION_DENIED`.
2. **Customer Service Creation/Tampering**: Regular signed-in user attempts to `create` or `update` `/services/resume_pro` -> `PERMISSION_DENIED`.
3. **Cross-User Order Read (PII Leak)**: Alice (`alice_123`) attempts to `get` or `list` Bob's (`bob_456`) order -> `PERMISSION_DENIED`.
4. **Unfiltered Order List Query**: Alice executes `db.collection("orders").get()` without `.where("userId", "==", "alice_123")` -> `PERMISSION_DENIED`.
5. **Identity Spoofing on Order Create**: Alice creates an order setting `userId: "bob_456"` -> `PERMISSION_DENIED`.
6. **Price Tampering on Order Create**: Alice creates an order for a ₹100 service but passes `priceSnapshot: 1` -> `PERMISSION_DENIED`.
7. **Ordering an Inactive Service**: Alice creates an order for a service with `active: false` -> `PERMISSION_DENIED`.
8. **Self-Verified Payment Shortcut**: Alice creates or updates an order with `paymentStatus: "verified"` -> `PERMISSION_DENIED`.
9. **State Shortcutting on Order Update**: Alice attempts to update her own order's `status` to `"completed"` -> `PERMISSION_DENIED`.
10. **Terminal State Mutation**: Alice attempts to update `upiReference` on an order whose `status` is already `"completed"` or `"cancelled"` -> `PERMISSION_DENIED`.
11. **Shadow Update (Ghost Field Injection)**: Alice updates her profile `/users/alice_123` with an extra field `isAdmin: true` or `role: "admin"` -> `PERMISSION_DENIED`.
12. **Email Spoofing Attack**: Attacker signs in with unverified email `adityakty77@gmail.com` (`email_verified: false`) and attempts admin service update -> `PERMISSION_DENIED`.
