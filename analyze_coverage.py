import csv

filename = 'target/site/jacoco/jacoco.csv'
try:
    with open(filename, 'r') as f:
        reader = csv.reader(f)
        header = next(reader)
        # GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,...
        
        missed_classes = []
        for row in reader:
            pkg = row[1]
            cls = row[2]
            inst_missed = int(row[3])
            branch_missed = int(row[5])
            
            if inst_missed > 0 or branch_missed > 0:
                missed_classes.append((pkg, cls, inst_missed, branch_missed))
        
        missed_classes.sort(key=lambda x: x[2], reverse=True)
        
        print(f"{'Package':<60} {'Class':<40} {'Inst Miss':<10} {'Br Miss':<10}")
        print("-" * 120)
        for pkg, cls, im, bm in missed_classes:
            print(f"{pkg:<60} {cls:<40} {im:<10} {bm:<10}")

except Exception as e:
    print(f"Error: {e}")
