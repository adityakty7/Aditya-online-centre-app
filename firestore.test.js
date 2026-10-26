const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ADMIN_UID = "admin_aditya";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function makeValidService(overrides = {}) {
  return {
    tenantId: "aditya_online_center",
    title: "Professional Resume Design",
    titleHi: "प्रोफेशनल रिज्यूमे डिज़ाइन",
    category: "resume",
    description: "ATS-friendly modern resume prepared at Aditya Online Center.",
    descriptionHi: "आदित्य ऑनलाइन सेंटर में तैयार आधुनिक रिज्यूमे।",
    price: 100,
    turnaroundTime: "2 Hours",
    requirements: ["Aadhaar Card", "Qualification Details"],
    imageUrl: "https://images.unsplash.com/photo-1586281380349-632531db7ed4",
    active: true,
    createdAt: new Date("2026-01-01T00:00:00Z"),
    updatedAt: new Date("2026-01-01T00:00:00Z"),
    ...overrides,
  };
}

function makeValidOrder(uid = ALICE_UID, overrides = {}) {
  return {
    orderNumber: "AOC-20261026-1001",
    userId: uid,
    customerUid: uid,
    customerName: "Rohan Kumar",
    customerPhone: "9876543210",
    customerEmail: "rohan@example.com",
    sourceChannel: "android",
    serviceId: "svc_resume",
    serviceTitleSnapshot: "Professional Resume Design",
    priceSnapshot: 100,
    applicantDetailsSummary: "B.Sc Graduate, Kateya Gopalganj Bihar",
    documentLinks: ["marksheet_12th.pdf"],
    upiReference: "",
    paymentStatus: "unpaid",
    status: "pending",
    adminNotes: "",
    deliveryFileUrl: "",
    createdAt: new Date("2026-01-01T00:00:00Z"),
    updatedAt: new Date("2026-01-01T00:00:00Z"),
    ...overrides,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
    await testEnv.withSecurityRulesDisabled(async (context) => {
      const db = context.firestore();
      await db.collection("admins").doc(ADMIN_UID).set({
        adminId: ADMIN_UID,
        createdAt: new Date("2026-01-01T00:00:00Z"),
      });
      await db.collection("services").doc("svc_resume").set(makeValidService());
      await db
        .collection("services")
        .doc("svc_inactive")
        .set(makeValidService({ active: false, price: 150 }));
    });
  }
});

test("1. Unauthenticated user cannot read services or orders", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb
      .collection("services")
      .where("tenantId", "==", "aditya_online_center")
      .get()
  );
  await assertFails(unauthDb.collection("orders").get());
});

test("2. Authenticated customer can query services with tenantId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb
      .collection("services")
      .where("tenantId", "==", "aditya_online_center")
      .get()
  );
});

test("3. Regular customer cannot create, update, or delete services", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("services").doc("svc_hack").set(makeValidService())
  );
  await assertFails(
    aliceDb.collection("services").doc("svc_resume").update({ price: 1 })
  );
  await assertFails(aliceDb.collection("services").doc("svc_resume").delete());
});

test("4. Authorized admin can create and update services", async () => {
  const adminDb = testEnv.authenticatedContext(ADMIN_UID).firestore();
  await assertSucceeds(
    adminDb.collection("services").doc("svc_pan").set(
      makeValidService({
        title: "PAN Card Assistance",
        category: "certificate",
        price: 120,
      })
    )
  );
});

test("5. Customer can create valid order for active service with matching priceSnapshot", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").doc("ord_101").set(makeValidOrder(ALICE_UID))
  );
});

test("6. Customer cannot tamper with priceSnapshot or order an inactive service", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  // Tampered price (10 instead of 100)
  await assertFails(
    aliceDb
      .collection("orders")
      .doc("ord_tamper")
      .set(makeValidOrder(ALICE_UID, { priceSnapshot: 10 }))
  );
  // Inactive service
  await assertFails(
    aliceDb
      .collection("orders")
      .doc("ord_inactive")
      .set(
        makeValidOrder(ALICE_UID, {
          serviceId: "svc_inactive",
          priceSnapshot: 150,
        })
      )
  );
});

test("7. Customer cannot self-verify payment or spoof another user's UID", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb
      .collection("orders")
      .doc("ord_spoof_paid")
      .set(makeValidOrder(ALICE_UID, { paymentStatus: "verified" }))
  );
  await assertFails(
    aliceDb
      .collection("orders")
      .doc("ord_spoof_uid")
      .set(makeValidOrder(BOB_UID))
  );
});

test("8. Cross-user order isolation and query alignment enforced", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("orders")
      .doc("bob_ord")
      .set(makeValidOrder(BOB_UID));
    await context
      .firestore()
      .collection("orders")
      .doc("alice_ord")
      .set(makeValidOrder(ALICE_UID));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  // Cannot read Bob's order
  await assertFails(aliceDb.collection("orders").doc("bob_ord").get());
  // Cannot list all orders without userId filter
  await assertFails(aliceDb.collection("orders").get());
  // Can list own orders with userId filter
  await assertSucceeds(
    aliceDb.collection("orders").where("userId", "==", ALICE_UID).get()
  );
});

test("9. Admin can update order status and verify payment; customer cannot mark order completed", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("orders")
      .doc("alice_ord")
      .set(makeValidOrder(ALICE_UID));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("orders").doc("alice_ord").update({
      status: "completed",
      updatedAt: new Date("2026-01-01T00:05:00Z"),
    })
  );

  const adminDb = testEnv.authenticatedContext(ADMIN_UID).firestore();
  await assertSucceeds(
    adminDb.collection("orders").doc("alice_ord").update({
      status: "completed",
      paymentStatus: "verified",
      adminNotes: "Verified UPI payment and uploaded final PDF.",
      deliveryFileUrl: "https://aditya-online-centre-jk87.vercel.app/deliveries/AOC-1001.pdf",
      updatedAt: new Date("2026-01-01T00:05:00Z"),
    })
  );
});

test("10. Email spoofing with unverified admin email is rejected", async () => {
  const spoofedAdminDb = testEnv
    .authenticatedContext("attacker_999", {
      email: "adityakty77@gmail.com",
      email_verified: false,
    })
    .firestore();
  await assertFails(
    spoofedAdminDb
      .collection("services")
      .doc("svc_resume")
      .update({ price: 0, updatedAt: new Date("2026-01-01T00:05:00Z") })
  );
});
