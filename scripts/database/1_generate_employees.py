import random

import pandas as pd


def generate_employees(num_employees=50):
    """Generate employee data for the banking system."""

    employees = []

    for i in range(1, num_employees + 1):
        age = random.randint(25, 60)
        monthly_income = round(random.uniform(4000, 7000), 2)
        credit_score = random.randint(700, 820)

        employee = {
            'name': f'Employee {i}',
            'email': f'employee{i}@example.com',
            'age': age,
            'monthly_income': monthly_income,
            'credit_score': credit_score,
            'role': 'EMPLOYEE',
            'password': '$2a$10$EblN0qSJB0xoga.FWp5Xuea2iXh4nL2Lk9QZcvrBekzvOft/Sifr6'  # employee123
        }
        employees.append(employee)

    # Convert to DataFrame and save to CSV
    df = pd.DataFrame(employees)
    df.to_csv('../db/data/employees.csv', index=False)
    print(f"Generated {num_employees} employee records")

if __name__ == "__main__":
    generate_employees()