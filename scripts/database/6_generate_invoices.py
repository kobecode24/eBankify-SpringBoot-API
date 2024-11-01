import pandas as pd
import random
from datetime import datetime, timedelta

def generate_invoices():
    """Generate invoice data for users."""

    invoices = []

    # Read existing user data
    users_df = pd.read_csv('../db/data/regular_users.csv')

    for _, user in users_df.iterrows():
        # Generate 1-3 invoices per user
        num_invoices = random.randint(1, 3)

        for _ in range(num_invoices):
            created_date = datetime.now() - timedelta(days=random.randint(0, 90))
            due_date = created_date + timedelta(days=random.randint(15, 45))

            invoice = {
                'amount_due': round(random.uniform(50, 1000), 2),
                'due_date': due_date.strftime('%Y-%m-%d'),
                'status': random.choices(
                    ['PENDING', 'PAID', 'OVERDUE'],
                    weights=[0.3, 0.6, 0.1]
                )[0],
                'user_id': user.name + 53,  # Offset for user IDs
                'created_at': created_date.strftime('%Y-%m-%d %H:%M:%S')
            }
            invoices.append(invoice)

    df = pd.DataFrame(invoices)
    df.to_csv('../db/data/invoices.csv', index=False)
    print(f"Generated {len(invoices)} invoice records")

if __name__ == "__main__":
    generate_invoices()