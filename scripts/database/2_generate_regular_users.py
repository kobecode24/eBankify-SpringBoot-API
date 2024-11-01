import pandas as pd
import random
from datetime import datetime

def generate_regular_users(num_users=1000):
    """Generate regular user data for the banking system."""

    users = []

    for i in range(1, num_users + 1):
        # Determine age group and corresponding attributes
        if i <= num_users * 0.25:  # Young adults (18-25)
            age = random.randint(18, 25)
            income = round(random.uniform(2000, 4000), 2)
            credit_score = random.randint(500, 700)
        elif i <= num_users * 0.5:  # Early career (26-40)
            age = random.randint(26, 40)
            income = round(random.uniform(4000, 8000), 2)
            credit_score = random.randint(600, 800)
        elif i <= num_users * 0.75:  # Mid-career (41-55)
            age = random.randint(41, 55)
            income = round(random.uniform(6000, 12000), 2)
            credit_score = random.randint(650, 850)
        else:  # Senior (56-75)
            age = random.randint(56, 75)
            income = round(random.uniform(3000, 7000), 2)
            credit_score = random.randint(580, 820)

        user = {
            'name': f'User {i}',
            'email': f'user{i}@example.com',
            'age': age,
            'monthly_income': income,
            'credit_score': credit_score,
            'role': 'USER',
            'password': '$2a$10$EblN0qSJB0xoga.FWp5Xuea2iXh4nL2Lk9QZcvrBekzvOft/Sifr6'  # user123
        }
        users.append(user)

    df = pd.DataFrame(users)
    df.to_csv('../db/data/regular_users.csv', index=False)
    print(f"Generated {num_users} regular user records")

if __name__ == "__main__":
    generate_regular_users()