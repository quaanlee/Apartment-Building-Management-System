with open("src/main/resources/static/js/manager/revenue.js", "r", encoding="utf-8") as f:
    lines = f.readlines()

# Remove donut section: lines 228-299 (0-indexed)
new_lines = []
for i, line in enumerate(lines):
    if 228 <= i <= 299:
        continue
    new_lines.append(line)

with open("src/main/resources/static/js/manager/revenue.js", "w", encoding="utf-8") as f:
    f.writelines(new_lines)

print("JS cleaned")
