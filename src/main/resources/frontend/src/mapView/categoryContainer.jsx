import React from "react";
import Styles from "./categoryContainer.module.css"

export function CategoryContainer({children}) {
    return <div className={Styles.container}>
        {children}
    </div>
}