with open("src/main/resources/static/css/manager/revenue.css", "r", encoding="utf-8") as f:
    css = f.read()

# Add overflow-y auto to rev-table-card
old = ".rev-table-card {\n    width: 100%;\n    max-width: 1558.4px;\n    height: 515px;\n    background: var(--color-surface-white);\n    border: 1px solid var(--color-outline-variant);\n    border-radius: 12px;\n    overflow: hidden;\n    box-shadow: 0 1px 4px rgba(0,0,0,0.04);\n}"
new = ".rev-table-card {\n    width: 100%;\n    max-width: 1558.4px;\n    height: 515px;\n    background: var(--color-surface-white);\n    border: 1px solid var(--color-outline-variant);\n    border-radius: 12px;\n    overflow-y: auto;\n    overflow: hidden;\n    box-shadow: 0 1px 4px rgba(0,0,0,0.04);\n}"
css = css.replace(old, new)

with open("src/main/resources/static/css/manager/revenue.css", "w", encoding="utf-8") as f:
    f.write(css)

print("Added overflow-y")
