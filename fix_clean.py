with open("src/main/resources/static/css/manager/revenue.css", "r", encoding="utf-8") as f:
    css = f.read()

# Remove remaining donut references
import re
css = re.sub(r"\.rev-donut-legend-left span:last-child \{(\s*[^}]*\s*)\}", "", css)
css = re.sub(r"\n{3,}", "\n\n", css)

with open("src/main/resources/static/css/manager/revenue.css", "w", encoding="utf-8") as f:
    f.write(css)

# JS: remove comment about donut
with open("src/main/resources/static/js/manager/revenue.js", "r", encoding="utf-8") as f:
    js = f.read()
js = js.replace("   - Bar chart (monthly trend)\n   - Donut chart (revenue by type)\n", "   - Bar chart (revenue by type)\n")
with open("src/main/resources/static/js/manager/revenue.js", "w", encoding="utf-8") as f:
    f.write(js)

print("Remaining cleanups done")
