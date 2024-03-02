import React from "react";

import Styles from "./sidebar.module.css";
import MapCategory from "./mapCategory";
import SidebarCategory from "./sidebarCategory";

class Sidebar extends React.Component {
    render() {
        return <div className={Styles.Sidebar}>
            <div className={Styles.SidebarInset}>
                <input className={Styles.SearchArea} type={"text"} placeholder={"Search Maps"} value={this.props.query} onChange={this.searchChanged.bind(this)}/>
                <div className={Styles.SidebarTitle}>My Maps</div>
                {["No Category", ...this.props.data.map(map => map.category ? map.category : "").filter(category => category !== "")].filter((category, index, array) => array.indexOf(category) === index).map(category => <SidebarCategory manager={this.props.manager} isCollection={false} category={category} />)}
                <div className={Styles.SidebarTitle}>Global Collections</div>
                {Object.keys(this.props.collections).map(collection => <SidebarCategory manager={this.props.manager} isCollection={true} category={collection} />)}
            </div>
        </div>
    }

    searchChanged(e) {
        this.props.onSearchUpdated(e.target.value);
    }
}

export default Sidebar;