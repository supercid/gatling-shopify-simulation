import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WebhookPayloadSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val random = new scala.util.Random

  def generateWebhookPayload(): String = {
    val id = random.nextLong(1000000000)
    val now = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    s"""
    {
      "admin_graphql_api_id": "gid://shopify/Product/$id",
      "body_html": "Example product description ${random.nextInt(1000)}",
      "created_at": "$now",
      "handle": "example-product-${random.alphanumeric.take(5).mkString}",
      "id": $id,
      "product_type": "Test Product",
      "published_at": "$now",
      "template_suffix": null,
      "title": "Example Product ${random.nextInt(1000)}",
      "updated_at": "$now",
      "vendor": "Test Vendor",
      "status": "active",
      "published_scope": "web",
      "tags": "test, example, gatling",
      "variants": [
        {
          "id": ${random.nextLong(1000000000)},
          "product_id": $id,
          "title": "Default Title",
          "price": "${random.nextInt(10000) / 100.0}",
          "sku": "SKU-${random.alphanumeric.take(8).mkString}",
          "position": 1,
          "inventory_policy": "deny",
          "compare_at_price": null,
          "fulfillment_service": "manual",
          "inventory_management": null,
          "option1": "Default Title",
          "option2": null,
          "option3": null,
          "created_at": "$now",
          "updated_at": "$now",
          "taxable": true,
          "barcode": null,
          "grams": ${random.nextInt(1000)},
          "image_id": null,
          "weight": ${random.nextInt(1000) / 100.0},
          "weight_unit": "kg",
          "inventory_item_id": ${random.nextLong(1000000000)},
          "inventory_quantity": ${random.nextInt(100)},
          "old_inventory_quantity": ${random.nextInt(100)},
          "requires_shipping": true
        }
      ],
      "options": [],
      "images": [],
      "image": null
    }
    """
  }

  val scn = scenario("WebhookPayload Simulation")
    .exec(
      http("Send Webhook Payload")
        .post("/webhook")
        .body(StringBody(_ => generateWebhookPayload()))
        .asJson
        .check(status.is(200))
    )

  setUp(
    scn.inject(
      rampUsers(100).during(30.seconds),
      constantUsersPerSec(10).during(1.minute)
    )
  ).protocols(httpProtocol)
}