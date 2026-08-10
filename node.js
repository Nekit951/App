const functions = require("firebase-functions");
const admin = require("firebase-admin");
const { YooCheckout } = require("yoomoney-sdk");

admin.initializeApp();

// Инициализация ЮKassa с вашими учетными данными магазина
const checkout = new YooCheckout({
    shopId: "YOUR_SHOP_ID",
    secretKey: "YOUR_SECRET_KEY"
});

exports.createPayment = functions.https.onCall(async (data, context) => {
    // Проверяем, авторизован ли пользователь в Firebase
    if (!context.auth) {
        throw new functions.https.HttpsError("unauthenticated", "User must be logged in.");
    }

    const { paymentToken, productId } = data;
    const userId = context.auth.uid;

    try {
        // 1. Извлекаем актуальную цену товара напрямую из Realtime Database
        const snapshot = await admin.database().ref(`products/${productId}`).once("value");
        
        if (!snapshot.exists()) {
            throw new functions.https.HttpsError("not-found", "Product not found.");
        }

        const product = snapshot.val();
        const price = parseFloat(product.price).toFixed(2); // Формат "0.00"

        // 2. Генерируем уникальный ключ идемпотентности для предотвращения дубликатов
        const idempotenceKey = `${userId}_${productId}_${Date.now()}`;

        // 3. Отправляем запрос на создание платежа в ЮKassa
        const payment = await checkout.createPayment({
            amount: {
                value: price,
                currency: "RUB"
            },
            payment_token: paymentToken,
            confirmation: {
                type: "redirect",
                return_url: "https://your-app-scheme://payment-confirm" // Ссылка возврата в приложение после 3DS
            },
            capture: true, // Автоматическое списание (true) или двухстадийная оплата (false)
            description: `Оплата товара: ${product.title}`
        }, idempotenceKey);

        // 4. Записываем информацию о созданном платеже в БД для отслеживания статуса
        await admin.database().ref(`orders/${userId}/${payment.id}`).set({
            productId: productId,
            amount: price,
            status: payment.status, // На данном этапе обычно "pending"
            createdAt: admin.database.ServerValue.TIMESTAMP
        });

        // Возвращаем результат клиенту (включая confirmation_url, если нужен 3DSecure)
        return {
            paymentId: payment.id,
            status: payment.status,
            confirmationUrl: payment.confirmation ? payment.confirmation.confirmation_url : null
        };

    } catch (error) {
        console.error("YooKassa Payment Error:", error);
        throw new functions.https.HttpsError("internal", error.message);
    }
});
