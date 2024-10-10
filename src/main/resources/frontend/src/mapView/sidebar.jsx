import React from "react";

import Styles from "./sidebar.module.css";
import MapCategory from "./mapCategory";
import SidebarCategory from "./sidebarCategory";
import DiskAccessComponent from "./diskAccess/diskAccessComponent";
import {Icon} from "../icon";

function SidebarTitle({children, isShowing, setShow, showName}) {
    return <div className={Styles.SidebarTitle} onClick={() => isShowing ? setShow("") : setShow(showName)} onDragEnter={() => !isShowing && setShow(showName)}>{children}<div className={Styles.SidebarLine} /><Icon icon={isShowing ? "arrow-up" : "arrow-down"} /></div>
}

function Category({showName, title, items, show, setShow}) {
    return <>
        <SidebarTitle isShowing={show === showName} setShow={setShow} showName={showName}>{title}</SidebarTitle>
        {show === showName && items}
    </>
}

class Sidebar extends React.Component {
    render() {
        return <div className={Styles.Sidebar}>
            <div className={Styles.SidebarInset}>
                <input className={Styles.SearchArea} type={"text"} placeholder={"Search Maps"} value={this.props.query} onChange={this.searchChanged.bind(this)}/>
                <Category title={"My Maps"} showName={"local"} show={this.props.show} setShow={this.props.setShow} items={["No Category", ...this.props.data.map(map => map.category ? map.category : "").filter(category => category !== "")].filter((category, index, array) => array.indexOf(category) === index).map(category => <SidebarCategory manager={this.props.manager} isCollection={false} category={category} />)} />
                <Category title={"Global Collections"} showName={"global"} show={this.props.show} setShow={this.props.setShow} items={Object.keys(this.props.collections).map(collection => <SidebarCategory manager={this.props.manager} isCollection={true} category={collection} />)} />

                <div style={{flexGrow: "1"}} />
            </div>
                <DiskAccessComponent manager={this.props.manager} data={this.props.data} />
        </div>
    }

    searchChanged(e) {
        this.props.onSearchUpdated(e.target.value);
    }
}

export default Sidebar;