import pandas as pd
import random
from datetime import datetime, timedelta

def generate_loans():
    """Generate loan data for eligible users."""

    loans = []

    # Read existing user data
    users_df = pd.read_csv('../db/data/regular_users.csv')
    eligible_users = users_df[users_df['credit_score'] >= 650]

    # Select 200 random eligible users
    selected_users = eligible_users.sample(n=min(200, len(eligible_users)))

    for _, user in selected_users.iterrows():
        principal = round(random.uniform(5000, 50000), 2)
        interest_rate = round(random.uniform(5, 15), 2)
        term = random.randint(12, 60)

        # Calculate monthly payment using loan amortization formula
        monthly_rate = interest_rate / 1200  # Convert annual rate to monthly decimal
        monthly_payment = (principal * monthly_rate * (1 + monthly_rate)**term) / ((1 + monthly_rate)**term - 1)

        start_date = datetime.now() - timedelta(days=random.randint(0, 365))

        loan = {
            'principal': principal,
            'interest_rate': interest_rate,
            'term_months': term,
            'monthly_payment': round(monthly_payment, 2),
            'remaining_amount': round(principal, 2),
            'status': random.choices(
                ['ACTIVE', 'COMPLETED', 'DEFAULTED'],
                weights=[0.7, 0.2, 0.1]
            )[0],
            'user_id': user.name + 53,  # Offset for user IDs
            'start_date': start_date.strftime('%Y-%m-%d'),
            'end_date': (start_date + timedelta(days=30*term)).strftime('%Y-%m-%d'),
            'guarantees': 'Sample guarantees for loan'
        }
        loans.append(loan)

    df = pd.DataFrame(loans)
    df.to_csv('../db/data/loans.csv', index=False)
    print(f"Generated {len(loans)} loan records")

if __name__ == "__main__":
    generate_loans()