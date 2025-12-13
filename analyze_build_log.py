def analyze(file_path):
    try:
        print(f"Analyzing {file_path}")
        with open(file_path, 'r', encoding='utf-16', errors='ignore') as f:
            lines = f.readlines()
            for i, line in enumerate(lines):
                line_lower = line.lower()
                if "[error] compilation error" in line_lower:
                    print(f"Line {i+1}: {line.strip()}")
                    for j in range(1, 50):
                        if i+j < len(lines):
                            print(f"  {lines[i+j].strip()}")
                    break
                elif "[error]" in line_lower or "failure" in line_lower or "exception" in line_lower:
                    print(f"Line {i+1}: {line.strip()}")
    except FileNotFoundError:
        print("Log file not found.")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    analyze("build_log.txt")
