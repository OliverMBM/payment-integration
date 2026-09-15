import csv
import sys
from collections import defaultdict
from decimal import Decimal

import requests
from requests.auth import HTTPBasicAuth


API_URL = "http://localhost:8080/payments"
USERNAME = "integration-user"
PASSWORD = "integration-pass"
OUTPUT_FILE = "payments_summary.csv"


def fetch_payments():
    try:
        response = requests.get(
            API_URL,
            auth=HTTPBasicAuth(USERNAME, PASSWORD),
            timeout=10
        )

        if response.status_code == 401:
            print("Authentication failed. Check username and password.")
            return None

        response.raise_for_status()

        return response.json()

    except requests.exceptions.ConnectionError:
        print("Could not connect to the payments API.")
        return None

    except requests.exceptions.Timeout:
        print("The request to the payments API timed out.")
        return None

    except requests.exceptions.RequestException as exception:
        print(f"Error calling payments API: {exception}")
        return None


def build_summary(payments):
    summary = defaultdict(
        lambda: {
            "total": Decimal("0"),
            "count": 0
        }
    )

    for payment in payments:
        customer_id = payment["customerId"]
        amount = Decimal(str(payment["amount"]))

        summary[customer_id]["total"] += amount
        summary[customer_id]["count"] += 1

    return summary


def write_csv(summary):
    with open(OUTPUT_FILE, "w", newline="", encoding="utf-8") as csv_file:
        writer = csv.writer(csv_file)

        writer.writerow([
            "customerId",
            "totalAmount",
            "paymentCount",
            "averageAmount"
        ])

        for customer_id, values in summary.items():
            total = values["total"]
            count = values["count"]
            average = total / count

            writer.writerow([
                customer_id,
                f"{total:.2f}",
                count,
                f"{average:.2f}"
            ])


def main():
    payments = fetch_payments()

    if payments is None:
        sys.exit(1)

    summary = build_summary(payments)

    write_csv(summary)

    print(f"Report generated successfully: {OUTPUT_FILE}")


if __name__ == "__main__":
    main()