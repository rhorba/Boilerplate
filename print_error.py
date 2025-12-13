
filename = 'role_test_log.txt'
try:
    with open(filename, 'r', encoding='utf-8') as f:
        lines = f.readlines()
except UnicodeDecodeError:
    with open(filename, 'r', encoding='utf-16') as f:
        lines = f.readlines()

printing = False
count = 0
for line in lines:
    if "Caused by:" in line or "java.lang.IllegalStateException" in line or "UnsatisfiedDependencyException" in line:
        printing = True
    if printing:
        print(line.rstrip())
        count += 1
        if count > 50:
            break
