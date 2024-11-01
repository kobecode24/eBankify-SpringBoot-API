# 3_generate_accounts.py
import pandas as pd
import random


def generate_accounts():
    """Generate account data for all users."""

    accounts = []

    # Read existing user data
    users_df = pd.read_csv('../db/data/regular_users.csv')
    employees_df = pd.read_csv('../db/data/employees.csv')

    # Admin accounts (assuming 2 admin users with IDs 1 and 2)
    for admin_id in range(1, 3):
        accounts.append({
            'account_id': len(accounts) + 1,  # Changed from accountId to account_id
            'balance': round(random.uniform(50000, 100000), 2),
            'status': 'ACTIVE',
            'user_id': admin_id  # Changed from userId to user_id
        })

    # Employee accounts
    for _, employee in employees_df.iterrows():
        # Primary account
        accounts.append({
            'account_id': len(accounts) + 1,  # Changed from accountId to account_id
            'balance': round(random.uniform(10000, 50000), 2),
            'status': 'ACTIVE',
            'user_id': employee.name + 3  # Changed from userId to user_id
        })

        # 50% chance of secondary account
        if random.random() < 0.5:
            accounts.append({
                'account_id': len(accounts) + 1,  # Changed from accountId to account_id
                'balance': round(random.uniform(5000, 20000), 2),
                'status': 'ACTIVE',
                'user_id': employee.name + 3  # Changed from userId to user_id
            })

    # Regular user accounts
    for _, user in users_df.iterrows():
        # Primary account (all users)
        accounts.append({
            'account_id': len(accounts) + 1,
            'balance': round(random.uniform(1000, 20000), 2),
            'status': 'ACTIVE',
            'user_id': user.name + 53
        })

        # Second account (60% chance)
        if random.random() < 0.6:
            accounts.append({
                'account_id': len(accounts) + 1,
                'balance': round(random.uniform(500, 15000), 2),
                'status': 'ACTIVE',
                'user_id': user.name + 53
            })

        # Third account (30% chance)
        if random.random() < 0.3:
            accounts.append({
                'account_id': len(accounts) + 1,
                'balance': round(random.uniform(100, 10000), 2),
                'status': 'ACTIVE',
                'user_id': user.name + 53
            })

    df = pd.DataFrame(accounts)
    df.to_csv('../db/data/accounts.csv', index=False)
    print(f"Generated {len(accounts)} account records")
    print("Account columns:", df.columns.tolist())  # Debug info


if __name__ == "__main__":
    generate_accounts()