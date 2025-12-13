
filename = 'backend_test_results_3.txt'
try:
    with open(filename, 'r', encoding='utf-16le') as f:
        lines = f.readlines()
except:
    with open(filename, 'r', encoding='utf-8') as f:
        lines = f.readlines()

for i, line in enumerate(lines):
    if "Caused by:" in line:
        print(f"--- MATCH at line {i} ---")
        for j in range(i, min(i + 50, len(lines))):
            print(lines[j].rstrip())
        print("-------------------------")
