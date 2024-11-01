import subprocess
import os
import time

def setup_directories():
    """Create necessary directories if they don't exist."""
    os.makedirs('../db/data', exist_ok=True)

def run_script(script_name):
    """Run a Python script and handle any errors."""
    print(f"\nRunning {script_name}...")
    try:
        subprocess.run(['python', script_name], check=True)
        print(f"Successfully completed {script_name}")
        return True
    except subprocess.CalledProcessError as e:
        print(f"Error running {script_name}: {e}")
        return False

def main():
    """Run all data generation scripts in order."""
    setup_directories()

    scripts = [
        '1_generate_employees.py',
        '2_generate_regular_users.py',
        '3_generate_accounts.py',
        '4_generate_transactions.py',
        '5_generate_loans.py',
        '6_generate_invoices.py'
    ]

    start_time = time.time()
    success = True

    for script in scripts:
        if not run_script(script):
            success = False
            break

    end_time = time.time()
    duration = round(end_time - start_time, 2)

    if success:
        print(f"\nAll data generation completed successfully in {duration} seconds")
    else:
        print("\nData generation failed")

if __name__ == "__main__":
    main()