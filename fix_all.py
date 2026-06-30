with open("src/main/resources/templates/manager/revenue/revenue_report.html", "r", encoding="utf-8") as f:
    lines = f.readlines()

# 1. Remove donut chart card lines 654-677 (0-indexed)
# 2. Remove donut data div + chart comment lines 827-838
# Build new content excluding donut sections

new_lines = []
skip_ranges = [(654, 677), (827, 838)]

for i, line in enumerate(lines):
    skip = False
    for start, end in skip_ranges:
        if start <= i <= end:
            skip = True
            break
    if not skip:
        new_lines.append(line)

with open("src/main/resources/templates/manager/revenue/revenue_report.html", "w", encoding="utf-8") as f:
    f.writelines(new_lines)

print("HTML cleaned")
