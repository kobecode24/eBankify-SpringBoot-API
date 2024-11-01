# 4_generate_transactions.py
import pandas as pd
import random
from datetime import datetime, timedelta


def generate_transactions(num_transactions=5000):
    """Generate transaction data between existing accounts."""

    try:
        # Read the accounts CSV to get valid account IDs
        accounts_df = pd.read_csv('../db/data/accounts.csv')
        print("Available columns in accounts.csv:", accounts_df.columns.tolist())

        valid_account_ids = accounts_df['account_id'].tolist()

        transactions = []

        for _ in range(num_transactions):
            # Select two different random account IDs from valid accounts
            source_id = random.choice(valid_account_ids)
            dest_id = random.choice([id for id in valid_account_ids if id != source_id])

            # Generate random date within last 30 days
            transaction_date = datetime.now() - timedelta(
                days=random.randint(0, 30),
                hours=random.randint(0, 23),
                minutes=random.randint(0, 59)
            )

            transaction = {
                'type': random.choices(['STANDARD', 'INSTANT'], weights=[0.7, 0.3])[0],
                'amount': round(random.uniform(10, 5000), 2),
                'source_account_id': source_id,
                'destination_account_id': dest_id,
                'status': 'COMPLETED',
                'created_at': transaction_date.strftime('%Y-%m-%d %H:%M:%S')
            }
            transactions.append(transaction)

        # Save to CSV
        df = pd.DataFrame(transactions)
        df.to_csv('../db/data/transactions.csv', index=False)
        print(f"Generated {num_transactions} transaction records")

    except Exception as e:
        print("Error generating transactions:", str(e))
        raise e


if __name__ == "__main__":
    generate_transactions()