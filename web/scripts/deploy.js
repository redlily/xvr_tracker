const fs = require("fs-extra");
const path = require("path");

const sourceDir = path.join(__dirname, "../src/plot_analyzed_data");
const deployDirs = [
    path.join(__dirname, "../../android/domain/src/main/assets/plot_analyzed_data"),
    path.join(__dirname, "../../ios/xvr_tracker/xvr_tracker/Assets/plot_analyzed_data"),
];

(async () => {
    for (const deployDir of deployDirs) {
        if (await fs.exists(deployDir)) {
            await fs.emptyDir(deployDir);
        } else {
            await fs.mkdirp(deployDir);
        }
        await fs.copy(sourceDir, deployDir);
    }
})();
